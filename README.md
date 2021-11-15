# tiny-records

An application for processing and displaying personnel records.

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
  $ ./target/tiny-records-2.0.0 --help
```

## Options

`tiny-records` defaults to running in cli-mode. To run it with the rest api available, pass the `web` argument:

```
  $ ./target/tiny-records-2.0.0 web --port <your-port-of-choice>
```

Full list of options:

Short Option | Long Option | Required? | Description |
------------ | ----------- | --------- | ----------- |
-h           | --help      | N         | Display help and exit |
-d           | --data      | Y         | Path to a directory or single file for processing (cli-mode only) |
-o           | --output    | Y         | Sort order for the displayed output (cli-mode only). Possible values are: view1 -> favorite-color + last-name, view2 -> date-of-birth, view3 -> last-name |
-p           | --port      | N         | Port to use for the web server (web-mode only) |

## Examples

### cli-mode

Parse a pipe-delimited file and view sorted by last name:

```
  $ ./target/tiny-records-2.0.0 --data ./test/sample-pipe-delimited.txt --output view3

|-------------+------------+---------------------------+----------------+---------------|
|  Last Name  | First Name |           Email           | Favorite Color | Date Of Birth |
|-------------+------------+---------------------------+----------------+---------------|
| wizard      | merlin     | bermuda@backwards.com     | blue           | 1/1/2467      |
| wart        | arthur     | king@england.co.uk        | red            | 7/1/0607      |
| the-owl     | archimedes | wise@owl.com              | brown          | 4/6/0287      |
| of-the-lake | lady       | swordbearer@england.co.uk | red            | 12/31/0411    |
| mordred     | sir        | traitor@england.co.uk     | black          | 12/31/0628    |
| gawain      | sir        | knight@england.co.uk      | green          | 12/10/0602    |
|-------------+------------+---------------------------+----------------+---------------|
```

Parse a comma-delimited file and view sorted by birth date:

```
  $ ./target/tiny-records-2.0.0 --data ./test/sample-comma-delimited.txt --output view2

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
  $ ./target/tiny-records-2.0.0 --data ./test/sample-space-delimited.txt --output view1

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
  $ ./target/tiny-records-2.0.0 --data ./missing.txt --output view5

Some of the provided arguments had errors:
Failed to validate "--data ./missing.txt": Must point to existing directory or .txt file!
Failed to validate "--output view5": Must be one of: view1, view2, or view3
```

So, too, will any missing arguments:

```
  $ ./target/tiny-records-2.0.0 --data ./missing.txt
Some of the provided arguments had errors:
Failed to validate "--data ./missing.txt": Must point to existing directory or .txt file!

  $ ./target/tiny-records-2.0.0  --output view5
Some of the provided arguments had errors:
Failed to validate "--output view5": Must be one of: view1, view2, or view3
```

### web-mode

Make sure the server is up and running:

```
  $ curl http://localhost:3001/status | jq '.'
  % Total    % Received % Xferd  Average Speed   Time    Time     Time  Current
                                 Dload  Upload   Total   Spent    Left  Speed
100    28  100    28    0     0   5600      0 --:--:-- --:--:-- --:--:--  5600

{
  "message": "Server running"
}
```

POST a new pipe-delimited record:

```
  $ curl -XPOST -H "Content-type: application/json"  http://localhost:3001/records -d '{"record-line": "the-owl|archimedes|wise@owl.com|brown|287-04-06"}' | jq '.'
  % Total    % Received % Xferd  Average Speed   Time    Time     Time  Current
                                 Dload  Upload   Total   Spent    Left  Speed
100   190  100   124  100    66   8266   4400 --:--:-- --:--:-- --:--:-- 12666

{
  "last-name": "the-owl",
  "first-name": "archimedes",
  "email": "wise@owl.com",
  "favorite-color": "brown",
  "date-of-birth": "4/6/0287"
}
```

POST a new comma-delimited record:

```
  $ curl -XPOST -H "Content-type: application/json"  http://localhost:3001/records -d '{"record-line": "gawain,sir,knight@england.co.uk,green,602-12-10"}' | jq '.'
  % Total    % Received % Xferd  Average Speed   Time    Time     Time  Current
                                 Dload  Upload   Total   Spent    Left  Speed
100   192  100   126  100    66   4846   2538 --:--:-- --:--:-- --:--:--  7384

{
  "last-name": "gawain",
  "first-name": "sir",
  "email": "knight@england.co.uk",
  "favorite-color": "green",
  "date-of-birth": "12/10/0602"
}
```

POST a new space-delimited record:

```
  $ curl -XPOST -H "Content-type: application/json"  http://localhost:3001/records -d '{"record-line": "mordred sir traitor@england.co.uk black 627-12-31"}' | jq '.'
  % Total    % Received % Xferd  Average Speed   Time    Time     Time  Current
                                 Dload  Upload   Total   Spent    Left  Speed
100   196  100   128  100    68   9142   4857 --:--:-- --:--:-- --:--:-- 14000

{
  "last-name": "mordred",
  "first-name": "sir",
  "email": "traitor@england.co.uk",
  "favorite-color": "black",
  "date-of-birth": "12/31/0628"
}
```

GET a list of records sorted by color:

```
  $ curl  http://localhost:3001/records/color | jq '.'
  % Total    % Received % Xferd  Average Speed   Time    Time     Time  Current
                                 Dload  Upload   Total   Spent    Left  Speed
100   394  100   394    0     0  23176      0 --:--:-- --:--:-- --:--:-- 23176

{
  "records": [
    {
      "last-name": "mordred",
      "first-name": "sir",
      "email": "traitor@england.co.uk",
      "favorite-color": "black",
      "date-of-birth": "12/31/0628"
    },
    {
      "last-name": "the-owl",
      "first-name": "archimedes",
      "email": "wise@owl.com",
      "favorite-color": "brown",
      "date-of-birth": "4/6/0287"
    },
    {
      "last-name": "gawain",
      "first-name": "sir",
      "email": "knight@england.co.uk",
      "favorite-color": "green",
      "date-of-birth": "12/10/0602"
    }
  ]
}
```

GET a list of records sorted by date of birth:

```
  $ curl  http://localhost:3001/records/birthdate | jq '.'
  % Total    % Received % Xferd  Average Speed   Time    Time     Time  Current
                                 Dload  Upload   Total   Spent    Left  Speed
100   394  100   394    0     0  35818      0 --:--:-- --:--:-- --:--:-- 35818

{
  "records": [
    {
      "last-name": "the-owl",
      "first-name": "archimedes",
      "email": "wise@owl.com",
      "favorite-color": "brown",
      "date-of-birth": "4/6/0287"
    },
    {
      "last-name": "gawain",
      "first-name": "sir",
      "email": "knight@england.co.uk",
      "favorite-color": "green",
      "date-of-birth": "12/10/0602"
    },
    {
      "last-name": "mordred",
      "first-name": "sir",
      "email": "traitor@england.co.uk",
      "favorite-color": "black",
      "date-of-birth": "12/31/0628"
    }
  ]
}
```

GET a list of records sorted by favorite last name:

```
  $ curl  http://localhost:3001/records/name | jq '.'
  % Total    % Received % Xferd  Average Speed   Time    Time     Time  Current
                                 Dload  Upload   Total   Spent    Left  Speed
100   394  100   394    0     0  26266      0 --:--:-- --:--:-- --:--:-- 26266

{
  "records": [
    {
      "last-name": "gawain",
      "first-name": "sir",
      "email": "knight@england.co.uk",
      "favorite-color": "green",
      "date-of-birth": "12/10/0602"
    },
    {
      "last-name": "mordred",
      "first-name": "sir",
      "email": "traitor@england.co.uk",
      "favorite-color": "black",
      "date-of-birth": "12/31/0628"
    },
    {
      "last-name": "the-owl",
      "first-name": "archimedes",
      "email": "wise@owl.com",
      "favorite-color": "brown",
      "date-of-birth": "4/6/0287"
    }
  ]
}
```

Try to POST a non-parseable body:

```
  $ curl -XPOST -H "Content-type: application/json"  http://localhost:3001/records -d '{"record-line": "this-is-not-a-record"}' | jq '.'
  % Total    % Received % Xferd  Average Speed   Time    Time     Time  Current
                                 Dload  Upload   Total   Spent    Left  Speed
100    90  100    51  100    39   3642   2785 --:--:-- --:--:-- --:--:--  6428
{
  "message": "Must send a valid record in the body!"
}
```

Try to POST a non-json body:

```
  $ curl -XPOST -H "Content-type: application/json"  http://localhost:3001/records -d '"i am not a record"' | jq '.'
  % Total    % Received % Xferd  Average Speed   Time    Time     Time  Current
                                 Dload  Upload   Total   Spent    Left  Speed
100    70  100    51  100    19  10200   3800 --:--:-- --:--:-- --:--:-- 14000
{
  "message": "Must send a valid record in the body!"
}
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

### cli-mode

Files for processing _must_ be plain-text, and end with `.txt`.

Each row of the file(s) to be processed must contain five fields, in order:

- Last Name
- First Name
- Email
- Favorite Color
- Date of Birth

Fields can be separated by either a pipe (`|`), a comma (`,`), or a space (` `).

Examples of properly-formatted files can be found in the `./test` directory.

### web-mode

New records can only be added, they cannot be updated.

POST bodies must be in json format, with the new record as a single string under the `record-line` key:

```
  $ curl -XPOST -H "Content-type: application/json" http://localhost:3000/records -d '{"record-line": "<your new record>"}'
```

Like for cli-mode, new record strings must contain all fields, and can only be pipe, comma, or space delimited.

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
