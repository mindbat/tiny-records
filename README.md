# tiny-records

A command-line utility for processing and displaying personnel records.

## Requirements

- git
- [leiningen](https://leiningen.org).
- [lein-bin](https://github.com/Raynes/lein-bin)

## Running Locally

Check out the code:

```
  $ git clone git@github.com:mindbat/tiny-records.git
```

Then build the binary:

```
  $ cd tiny-records
  $ lein bin
```

Now run it!

```
  $ ./target/tiny-records-0.1.0 --help
```

## Options

Short Option | Long Option | Required? | Description |
------------ | ----------- | --------- | ----------- |
-h           | --help      | N         | Display help and exit |
-d           | --data      | Y         | Path to a directory or single file for processing |
-o           | --output    | Y         | Sort order for the displayed output. Possible values are: view1 -> favorite-color + last-name, view2 -> date-of-birth, view3 -> last-name |

## Examples

Parse a pipe-delimited file and view sorted by last name:

```
  $ ./target/tiny-records-1.0.0 --data ./test/sample-pipe-delimited.txt --output view3

|-------------+------------+---------------------------+----------------+---------------|
|  Last Name  | First Name |           Email           | Favorite Color | Date Of Birth |
|-------------+------------+---------------------------+----------------+---------------|
| gawain      | sir        | knight@england.co.uk      | green          | 12/10/0602    |
| mordred     | sir        | traitor@england.co.uk     | black          | 12/31/0628    |
| of-the-lake | lady       | swordbearer@england.co.uk | red            | 12/31/0411    |
| the-owl     | archimedes | wise@owl.com              | brown          | 4/6/0287      |
| wart        | arthur     | king@england.co.uk        | red            | 7/1/0607      |
| wizard      | merlin     | bermuda@backwards.com     | blue           | 1/1/2467      |
|-------------+------------+---------------------------+----------------+---------------|
```

Parse a comma-delimited file and view sorted by birth date:

```
  $ ./target/tiny-records-1.0.0 --data ./test/sample-comma-delimited.txt --output view2

|-------------+------------+---------------------------+----------------+---------------|
|  Last Name  | First Name |           Email           | Favorite Color | Date Of Birth |
|-------------+------------+---------------------------+----------------+---------------|
| the-owl     | archimedes | wise@owl.com              | brown          | 4/6/0287      |
| of-the-lake | lady       | swordbearer@england.co.uk | red            | 12/31/0411    |
| gawain      | sir        | knight@england.co.uk      | green          | 12/10/0602    |
| wart        | arthur     | king@england.co.uk        | red            | 7/1/0607      |
| mordred     | sir        | traitor@england.co.uk     | black          | 12/31/0628    |
| wizard      | merlin     | bermuda@backwards.com     | blue           | 1/1/2467      |
|-------------+------------+---------------------------+----------------+---------------|
```

Parse a space-delimited file and view sorted by favorite color and last name:

```
  $ ./target/tiny-records-1.0.0 --data ./test/sample-space-delimited.txt --output view1

|-------------+------------+---------------------------+----------------+---------------|
|  Last Name  | First Name |           Email           | Favorite Color | Date Of Birth |
|-------------+------------+---------------------------+----------------+---------------|
| mordred     | sir        | traitor@england.co.uk     | black          | 12/31/0628    |
| wizard      | merlin     | bermuda@backwards.com     | blue           | 1/1/2467      |
| the-owl     | archimedes | wise@owl.com              | brown          | 4/6/0287      |
| gawain      | sir        | knight@england.co.uk      | green          | 12/10/0602    |
| of-the-lake | lady       | swordbearer@england.co.uk | red            | 12/31/0411    |
| wart        | arthur     | king@england.co.uk        | red            | 7/1/0607      |
|-------------+------------+---------------------------+----------------+---------------|
```

Input that points to a missing file or an invalid output view will be rejected:

```
  $ ./target/tiny-records-1.0.0 --data ./missing.txt --output view5

Some of the provided arguments had errors:
Failed to validate "--data ./missing.txt": Must point to existing directory or .txt file!
Failed to validate "--output view5": Must be one of: view1, view2, or view3
```

So, too, will any missing arguments:

```
  $ ./target/tiny-records-1.0.0 --data ./missing.txt
Some of the provided arguments had errors:
Failed to validate "--data ./missing.txt": Must point to existing directory or .txt file!

  $ ./target/tiny-records-1.0.0  --output view5
Some of the provided arguments had errors:
Failed to validate "--output view5": Must be one of: view1, view2, or view3
```

## Local Testing

Check out the code:

```
  $ git clone git@github.com:mindbat/tiny-records.git
```

Then fetch the dependencies and run the tests:

```
  $ cd tiny-records
  $ lein deps
  $ lein test
```

## Limitations

Files for processing _must_ be plain-text, and end with `.txt`.

Each row of the file(s) to be processed must contain five fields, in order:

- Last Name
- First Name
- Email
- Favorite Color
- Date of Birth

Fields can be separated by either a pipe (`|`), a comma (`,`), or a space (` `).

Examples of properly-formatted files can be found in the `./test` directory.

## License

Copyright © 2021 Ron Toland

This program and the accompanying materials are made available under the
terms of the Eclipse Public License 2.0 which is available at
http://www.eclipse.org/legal/epl-2.0.

This Source Code may also be made available under the following Secondary
Licenses when the conditions for such availability set forth in the Eclipse
Public License, v. 2.0 are satisfied: GNU General Public License as published by
the Free Software Foundation, either version 2 of the License, or (at your
option) any later version, with the GNU Classpath Exception which is available
at https://www.gnu.org/software/classpath/license.html.
