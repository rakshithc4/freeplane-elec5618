import sys
import re
from collections import defaultdict


def parse_theme_logs(filepath):
    transitions = []

    pattern = re.compile(r"from=(\w+)\s+to=(\w+)")

    with open(filepath, "r") as file:
        for line in file:
            line = line.strip()
            if not line:
                continue
            match = pattern.search(line)
            if match:
                from_state = match.group(1)
                to_state = match.group(2)
                transitions.append((from_state, to_state))

    return transitions


def compute_transition_counts(transitions):
    counts = defaultdict(lambda: defaultdict(int))

    for from_state, to_state in transitions:
        counts[from_state][to_state] += 1

    return counts


def compute_probabilities(counts):
    probabilities = {}

    for from_state, destinations in counts.items():
        total = sum(destinations.values())

        probabilities[from_state] = {}

        for to_state, count in destinations.items():
            probabilities[from_state][to_state] = count / total

    return probabilities


def print_transition_matrix(probabilities, states):
    states = sorted(states)

    print("\nMARKOV CHAIN TRANSITION PROBABILITY MATRIX")
    print("=" * 60)

    print(f"{'FROM / TO':<18}", end="")
    for state in states:
        print(f"{state:<18}", end="")
    print()

    print("-" * (18 + 18 * len(states)))

    for from_state in states:
        print(f"{from_state:<18}", end="")

        for to_state in states:
            probability = probabilities.get(from_state, {}).get(to_state, 0.0)
            print(f"{probability:<18.4f}", end="")

        print()

    print("=" * 60)


def print_transition_list(probabilities):
    print("\nTRANSITION PROBABILITIES")
    print("=" * 60)

    for from_state, destinations in sorted(probabilities.items()):
        for to_state, probability in sorted(destinations.items()):
            print(
                f"P({from_state} -> {to_state}) = "
                f"{probability:.4f} ({probability * 100:.1f}%)"
            )


def main():
    filepath = sys.argv[1] if len(sys.argv) > 1 else "freeplane_theme_transitions.log"

    print(f"[INFO] Reading log file: {filepath}")

    transitions = parse_theme_logs(filepath)

    print(f"[INFO] Total transitions found: {len(transitions)}")

    states = set()

    for from_state, to_state in transitions:
        states.add(from_state)
        states.add(to_state)

    counts = compute_transition_counts(transitions)
    probabilities = compute_probabilities(counts)

    print_transition_matrix(probabilities, states)
    print_transition_list(probabilities)


if __name__ == "__main__":
    main()
