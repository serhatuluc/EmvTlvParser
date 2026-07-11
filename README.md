# EMV TLV Parser

A small desktop tool that decodes BER-TLV encoded EMV chip card data (as found in ISO8583
Field 55 of a card transaction) into a readable tag / length / value table, annotated with
the standard EMVCo tag names (Book 3/4, Annex A).

![Java](https://img.shields.io/badge/Java-17%2B-orange)
![JavaFX](https://img.shields.io/badge/JavaFX-21-blue)

## Why

EMV chip transactions carry a chunk of BER-TLV encoded data describing the card application,
cryptogram, terminal verification results, etc. Reading this by hand means manually walking a
byte stream, tracking multi-byte tags, long-form lengths, and nested (constructed) tags. This
tool does that walk for you and flags any tag it doesn't recognize.

## Features

- Full BER-TLV parser: multi-byte tag numbers, short- and long-form lengths, nested
  (constructed) tags rendered as an indented tree.
- ~140 standard EMVCo tags with human-readable names.
- Unrecognized / proprietary tags are highlighted in red so they're easy to spot.
- Constructed (parent) tags are bolded to visually separate structure from leaf values.
- **Extensible tag list without rebuilding**: add proprietary tags, regional tags, or
  translated meanings via a plain `custom-tags.json` file - see below.
- Click any row in the results table to open a detail popup with the tag's full name, class,
  type, length, and hex/ASCII value.

## Adding your own tags (`custom-tags.json`)

On first run, the app writes a template file named `custom-tags.json` in the current working
directory (the project root when launched via `run.bat` or `mvn javafx:run` from there - **not**
`target/classes`, since that gets wiped by `mvn clean`).

The exact file location is shown in the blue box at the top of the app window - open and edit
it directly in a text editor (there is no in-app button for this):

```json
{
  "9F7C": "Merchant Custom Data (our own interpretation)",
  "DFAA": "Our own proprietary tag"
}
```

- Key: the tag in hex (case-insensitive), value: the name shown in the table.
- A custom entry for a tag that's already in the built-in EMVCo list **overrides** the
  built-in name (handy for translations or house terminology).
- Restart the app (or re-open it) to pick up changes - the file is read once at startup.
- The exact file location is shown in the blue box at the top of the app window, and an error
  is shown there instead if the file has invalid JSON (the app still runs fine with just the
  built-in list in that case).

## Running it

Requires JDK 17+.

```bash
mvn javafx:run
```

## Building a runnable jar

```bash
mvn clean package
```

## Running the tests

```bash
mvn test
```

## Screenshot

_(add a screenshot here)_

## Not included

This is a from-scratch, standalone reimplementation for learning/portfolio purposes - it has
no dependency on, or affiliation with, any specific card processing platform. The EMVCo tag
list is public information (EMV Book 3/4, Annex A).

## License

MIT - see [LICENSE](LICENSE).
