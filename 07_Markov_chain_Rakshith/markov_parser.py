import sys
from collections import defaultdict

# ── STEP 1: Parse log file ───────────────────────────────────────
def parse_logs(filepath):
    user_sequences = defaultdict(list)
    with open(filepath, 'r') as f:
        for line in f:
            line = line.strip()
            if not line or line.startswith('#'):
                continue
            parts = [p.strip() for p in line.split('|')]
            if len(parts) >= 4:
                user  = parts[1]
                state = parts[2]
                user_sequences[user].append(state)
    return user_sequences

# ── STEP 2: Count transitions ────────────────────────────────────
def compute_transition_counts(sequences):
    counts = defaultdict(lambda: defaultdict(int))
    for i in range(len(sequences) - 1):
        counts[sequences[i]][sequences[i + 1]] += 1
    return counts

# ── STEP 3: Compute probabilities ────────────────────────────────
def compute_probabilities(counts):
    probs = {}
    for from_state, transitions in counts.items():
        total = sum(transitions.values())
        probs[from_state] = {to: c / total for to, c in transitions.items()}
    return probs

# ── STEP 4: Print matrix ─────────────────────────────────────────
def print_probability_matrix(probs, all_states):
    print('\n' + '=' * 70)
    print('MARKOV CHAIN TRANSITION PROBABILITY MATRIX')
    print('=' * 70)

    states = sorted(all_states)
    col_w  = 16

    # Header row
    header = 'FROM \ TO'
    print(header.ljust(20), end='')
    for s in states:
        print(s[:col_w].ljust(col_w), end='')
    print()
    print('-' * (20 + col_w * len(states)))

    # Data rows
    for fs in states:
        print(fs.ljust(20), end='')
        for ts in states:
            prob = probs.get(fs, {}).get(ts, 0.0)
            print(str(round(prob, 4)).ljust(col_w), end='')
        print()

    print('=' * 70)

# ── STEP 5: Main ─────────────────────────────────────────────────
def main():
    filepath = sys.argv[1] if len(sys.argv) > 1 else 'freeplane_events.log'
    print('[INFO] Parsing: ' + filepath)

    user_sequences = parse_logs(filepath)
    all_states = set()

    for user, seq in user_sequences.items():
        print('\n[USER] ' + user + '  |  Events: ' + str(len(seq)))

        counts = compute_transition_counts(seq)
        probs  = compute_probabilities(counts)

        for s in seq:
            all_states.add(s)

        print_probability_matrix(probs, all_states)

        print('\n[TRANSITIONS for ' + user + ']')
        for fs, ts_dict in sorted(probs.items()):
            for ts, prob in sorted(ts_dict.items()):
                pct = round(prob * 100, 1)
                print('  P(' + fs + ' -> ' + ts + ') = ' + str(round(prob, 4)) + ' (' + str(pct) + '%)')

if __name__ == '__main__':
    main()