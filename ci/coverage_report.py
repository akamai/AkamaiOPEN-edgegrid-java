#!/usr/bin/env python3
"""Generate a coverage summary markdown table from JaCoCo XML files.

Usage:
    python ci/coverage_report.py <version>:<directory> ...

Each positional argument is a colon-separated pair of a Java version label and
a root directory that contains one or more per-module JaCoCo XML reports at
<module>/target/site/jacoco/jacoco.xml.  The script aggregates the top-level
LINE counters across all modules found under each directory and prints a
markdown table to stdout.
"""

import argparse
import sys
import xml.etree.ElementTree as ET
from pathlib import Path


def aggregate_line_coverage(directory: str) -> float | None:
    """Return the aggregated line coverage rate (0–1) from all jacoco.xml files
    under *directory*, or None when no reports are found."""
    xml_files = list(Path(directory).glob("**/target/site/jacoco/jacoco.xml"))
    if not xml_files:
        return None

    total_covered = 0
    total_missed = 0

    for xml_path in xml_files:
        try:
            tree = ET.parse(xml_path)  # noqa: S314 — file is a trusted CI artifact
        except ET.ParseError as exc:
            raise SystemExit(f"Failed to parse {str(xml_path)!r}: {exc}") from exc
        root = tree.getroot()
        # The top-level <counter type="LINE"> is a direct child of <report> and
        # gives the total for that module.
        for child in root:
            if child.tag == "counter" and child.attrib.get("type") == "LINE":
                try:
                    total_covered += int(child.attrib["covered"])
                    total_missed += int(child.attrib["missed"])
                except (KeyError, ValueError) as exc:
                    raise SystemExit(
                        f"Invalid LINE counter in {str(xml_path)!r}: {exc}"
                    ) from exc
                break

    total = total_covered + total_missed
    if total == 0:
        return 0.0
    return total_covered / total


def build_table(rows: list[tuple[str, float | None]]) -> str:
    lines = [
        "## Coverage Report",
        "",
        "| Java Version | Line Coverage |",
        "| :---: | :---: |",
    ]
    for version, rate in sorted(rows, key=lambda r: int(r[0]) if r[0].isdigit() else r[0]):
        if rate is None:
            lines.append(f"| {version} | ⚠️ n/a |")
        else:
            pct = rate * 100
            badge = "✅" if pct >= 80 else "❌"
            lines.append(f"| {version} | {badge} {pct:.1f}% |")
    return "\n".join(lines) + "\n"


def main() -> None:
    parser = argparse.ArgumentParser(
        description="Summarise JaCoCo coverage reports into a GitHub step summary."
    )
    parser.add_argument(
        "entries",
        nargs="+",
        metavar="VERSION:DIRECTORY",
        help="Colon-separated pair of a Java version label and the directory "
        "containing per-module JaCoCo XML files.",
    )
    args = parser.parse_args()

    rows: list[tuple[str, float | None]] = []
    for entry in args.entries:
        if ":" not in entry:
            print(f"Error: expected VERSION:DIRECTORY, got {entry!r}", file=sys.stderr)
            sys.exit(1)
        version, directory = entry.split(":", 1)
        rate = aggregate_line_coverage(directory)
        if rate is None:
            print(
                f"::warning::No JaCoCo XML reports found for Java {version}: {directory}",
                file=sys.stderr,
            )
        rows.append((version, rate))

    table = build_table(rows)
    print(table)


if __name__ == "__main__":
    main()
