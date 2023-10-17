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

import fnmatch
import re
import sys
import typing
import subprocess


def get_tracked_files() -> typing.List[str]:
    """Returns a list of all files tracked by Git."""

    cmd = ["git", "ls-files"]
    output = subprocess.check_output(cmd).decode("utf-8")
    return output.splitlines()


def check_license(file: str) -> bool:
    with open(file, "r") as f:
        contents = f.read()
        return "Licensed under the Apache License" in contents


matchers = [
    re.compile(fnmatch.translate('*.kt')),
    re.compile(fnmatch.translate('*.py')),
]


def should_check(file: str) -> bool:
    for matcher in matchers:
        if matcher.match(file):
            return True
    return False


def main():
    files = get_tracked_files()

    errors: typing.List[str] = []
    for file in files:
        if should_check(file):
            if not check_license(file):
                errors.append(f"❌ File \"{file}\" does not contain the license phrase")
        else:
            print(f"ℹ️ Skipping check for \"{file}\"")

    for error in errors:
        print(error, file=sys.stderr)
    if errors:
        exit(1)
    else:
        print("✅ All files contain the license phrase")
        exit(0)


if __name__ == "__main__":
    main()
