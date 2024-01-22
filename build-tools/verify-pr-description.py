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

import sys
import re
import os


def get_text_for_header(markdown_text, header):
    """Returns all text for a given header from a Markdown.

    Args:
      markdown_text: The path to the Markdown file.
      header: The header to get the text for.

    Returns:
      A string containing all text for the given header, or None if the header is
      not found.
    """
    header_pattern = r'^#{1,6}\s+(.*?)$'
    header_matches = re.finditer(header_pattern, markdown_text, re.MULTILINE)

    for match in header_matches:
        current_header = match.group(1)
        if header in current_header:
            next_match = next(header_matches, None)
            if next_match:
                end_pos = next_match.start()
            else:
                end_pos = len(markdown_text)
            # Extract text between headers
            return markdown_text[match.end():end_pos].strip()
    return None


def remove_html_comments(text):
    return re.sub(r"<!--(.*?)-->", "", text, flags=re.DOTALL)


def main():
    title = os.environ['TITLE']
    body = os.environ['BODY']
    errors = []

    if not re.search(r'LD-[0-9]+', title):
        errors.append('The title should contain a link to a jira task')
    if not re.search(r'LD-[0-9]+', body):
        errors.append('The description should contain a link to a jira ticket')
    if "LD-XXX" in body:
        errors.append('LD-XXX still is in the PR description')

    body_without_comments = remove_html_comments(body)
    if not get_text_for_header(body_without_comments, "Why?"):
        errors.append('Please fill "Why?" section')
    if not get_text_for_header(body_without_comments, "What?"):
        errors.append('Please fill "What?" section')

    if "Describe what is expected to happen" in body or "Describe step 1" in body:
        errors.append('Please fill "How to test" section')

    if "Add link here" in body or "Add here other useful documentation links" in body:
        errors.append('Please fill "Documentation" section')

    if "|               |              |" in body or not get_text_for_header(body_without_comments, "Demo"):
        errors.append('Please fill "Demo" section')

    unchecked_checkboxes = re.finditer(r'^- \[ ]\s+(.*?)$', body, re.MULTILINE)
    for match in unchecked_checkboxes:
        checkbox = match.group(1).strip()
        errors.append(f'Please ensure: "{checkbox}"')

    for error in errors:
        print(f"❌️ Error: {error}", file=sys.stderr)

    if errors:
        sys.exit(1)


if __name__ == "__main__":
    main()
