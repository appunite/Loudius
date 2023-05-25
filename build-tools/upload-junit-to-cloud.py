from google.cloud import bigquery
import xml.etree.ElementTree as ET
import argparse

# Uploading JUnit test results to BigQuery
def upload(final: bool):
    client = bigquery.Client()

    dataset_id = 'test_results'
    table_id = 'my_table'

    tree = ET.parse('build/test-results/results.xml')
    root = tree.getroot()
    timestamp = root.attrib['timestamp']

    rows = []
    for testcase in root.iter('testcase'):
        success = len(testcase.findall('failure')) == 0
        row = {
            'timestamp': timestamp,
            'testcase_final': final,
            'testcase_name': testcase.attrib['name'],
            'testcase_classname': testcase.attrib['classname'],
            'testcase_time': float(testcase.attrib['time']),
            'testcase_status_success': success
        }
        rows.append(row)

    dataset_ref = client.dataset(dataset_id)
    dataset = bigquery.Dataset(dataset_ref)
    table_ref = dataset.table(table_id)
    schema = [
        bigquery.SchemaField("timestamp", "TIMESTAMP"),
        bigquery.SchemaField('testcase_final', 'BOOLEAN', mode='REQUIRED'),
        bigquery.SchemaField('testcase_name', 'STRING', mode='REQUIRED'),
        bigquery.SchemaField('testcase_classname', 'STRING', mode='REQUIRED'),
        bigquery.SchemaField('testcase_time', 'FLOAT', mode='REQUIRED'),
        bigquery.SchemaField('testcase_status_success', 'BOOLEAN', mode='REQUIRED')
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
args = parser.parse_args()

upload(args.final)