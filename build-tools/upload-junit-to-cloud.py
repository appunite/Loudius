# Copyright 2023 AppUnite S.A.
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#     http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.

import typing

from google.cloud import bigquery
import xml.etree.ElementTree as ET
import argparse
from typing import List, Optional
import glob


# Uploading JUnit test results to BigQuery
def upload(final: bool, dummy: bool, url: Optional[str], files: List[typing.TextIO]):
    client = bigquery.Client()

    dataset_id = 'test_results'
    table_id = 'my_table'

    rows = []
    for file in files:
        tree = ET.parse(file)
        root = tree.getroot()
        timestamp = root.attrib['timestamp']
        test_cases = 0

        for testcase in root.iter('testcase'):
            success = len(testcase.findall('failure')) == 0
            failures = []
            for failure in testcase.findall('failure'):
                failures.append({
                    'message': failure.attrib.get('message', ''),
                    'type': failure.attrib.get('type', ''),
                    'content': failure.text,
                })
            row = {
                'timestamp': timestamp,
                'testcase_url': url,
                'testcase_final': final,
                'testcase_name': testcase.attrib['name'],
                'testcase_classname': testcase.attrib['classname'],
                'testcase_time': float(testcase.attrib['time']),
                'testcase_status_success': success,
                'testcase_failures': failures,
            }
            rows.append(row)
            test_cases += 1
        print(f"Read \"{file.name}\" file with {test_cases} tests")

    if dummy:
        print("Exiting without actions")
        return

    dataset_ref = client.dataset(dataset_id)
    dataset = bigquery.Dataset(dataset_ref)
    table_ref = dataset.table(table_id)
    schema = [
        bigquery.SchemaField("timestamp", "TIMESTAMP"),
        bigquery.SchemaField('testcase_final', 'BOOLEAN', mode='REQUIRED'),
        bigquery.SchemaField('testcase_name', 'STRING', mode='REQUIRED'),
        bigquery.SchemaField('testcase_classname', 'STRING', mode='REQUIRED'),
        bigquery.SchemaField('testcase_time', 'FLOAT', mode='REQUIRED'),
        bigquery.SchemaField('testcase_status_success', 'BOOLEAN', mode='REQUIRED'),
        bigquery.SchemaField('testcase_url', 'STRING', mode='NULLABLE'),
        bigquery.SchemaField('testcase_failures', 'RECORD', mode='REPEATED', fields=[
            bigquery.SchemaField('message', 'STRING', mode='NULLABLE'),
            bigquery.SchemaField('type', 'STRING', mode='NULLABLE'),
            bigquery.SchemaField('content', 'STRING', mode='NULLABLE'),
        ]),
    ]
    table = bigquery.Table(table_ref, schema=schema)

    def table_exists(table_ref):
        try:
            client.get_table(table_ref)
            return True
        except Exception as e:
            if "Not found" in str(e):
                return False
            else:
                raise e

    if not table_exists(table_ref):
        client.create_table(table)

    errors = client.insert_rows(table, rows)
    if errors:
        raise Exception(f'Encountered errors while uploading to BigQuery: {errors}')
    else:
        print('Successfully uploaded to BigQuery!')


parser = argparse.ArgumentParser()
parser.add_argument('--final', action='store_true', help='Enable final mode')
parser.add_argument('--dummy', action='store_true', help='Do not upload data')
parser.add_argument('--url')
parser.add_argument('--glob', type=str, required=False)
parser.add_argument('file', type=argparse.FileType('r'), nargs='*')
args = parser.parse_args()

all_files: List[typing.TextIO] = args.file
if args.glob:
    glob_files = glob.glob(args.glob, recursive=True)
    if not glob_files:
        parser.error(f"Could not find any file matching {args.glob}")
    glob_open_files = [open(file, "r") for file in glob_files]
    all_files.extend(glob_open_files)
if not all_files:
    parser.error(f"You need to specify --glob or file to upload")
upload(
    final=args.final,
    dummy=args.dummy,
    url=args.url,
    files=args.file
)
