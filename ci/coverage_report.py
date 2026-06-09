#!/usr/bin/env python3
import sys
import xml.etree.ElementTree as ET
from pathlib import Path

def aggregate_line_coverage(directory: str) -> float | None:
    xml_files = list(Path(directory).glob("**/target/site/jacoco/jacoco.xml"))
    if not xml_files:
        return None

    total_covered = total_missed = 0
    for xml_path in xml_files:
        # Finds the top-level <counter type="LINE"> directly under <report>
        counter = ET.parse(xml_path).getroot().find("./counter[@type='LINE']")
        if counter is not None:
            total_covered += int(counter.attrib["covered"])
            total_missed += int(counter.attrib["missed"])

    total = total_covered + total_missed
    return total_covered / total if total > 0 else 0.0

def main() -> None:
    if len(sys.argv) < 2:
        print("Usage: coverage_report.py <version>:<directory> ...", file=sys.stderr)
        sys.exit(1)

    rows = []
    for entry in sys.argv[1:]:
        if ":" not in entry:
            print(f"Error: expected VERSION:DIRECTORY, got {entry!r}", file=sys.stderr)
            sys.exit(1)

        version, directory = entry.split(":", 1)
        rate = aggregate_line_coverage(directory)

        if rate is None:
            print(f"::warning::No JaCoCo XML reports found for Java {version}: {directory}", file=sys.stderr)
        rows.append((version, rate))

    # Print Markdown table header
    print("## Coverage Report\n\n| Java Version | Line Coverage |\n| :---: | :---: |")

    # Sort natively: numerically if digits, alphabetically otherwise
    for version, rate in sorted(rows, key=lambda r: int(r[0]) if r[0].isdigit() else r[0]):
        if rate is None:
            print(f"| {version} | ⚠️ n/a |")
        else:
            pct = rate * 100
            badge = "✅" if pct >= 80 else "❌"
            print(f"| {version} | {badge} {pct:.1f}% |")

if __name__ == "__main__":
    main()
