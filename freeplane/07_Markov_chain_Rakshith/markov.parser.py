# Markov Chain Log Parser for ELEC5618
# This script parses Freeplane node editing logs and computes transition probabilities

import sys
import re
from collections import defaultdict

def parse_logs(filepath):
    """Parse log file and extract (user, state) sequences."""
    user_sequences = defaultdict(list)
    
    with open(filepath, 'r') as f:
        for line in f:
            line = line.strip()
            if not line or line.startswith('#'):
                continue
            # Expected format: TIMESTAMP | USER | STATE | EVENT
            parts = [p.strip() for p in line.split('|')]
            if len(parts) >= 3:
                user = parts[1] if len(parts) >= 4 else 'default_user'
                state = parts[2] if len(parts) >= 4 else parts[1]
                user_sequences[user].append(state)
    
    return user_sequences

def compute_transition_counts(sequences):
    """Count transitions between state pairs."""
    transition_counts = defaultdict(lambda: defaultdict(int))
    
    for i in range(len(sequences) - 1):
        from_state = sequences[i]
        to_state   = sequences[i + 1]
        transition_counts[from_state][to_state] += 1
    
    return transition_counts

def compute_probabilities(transition_counts):
    """Convert counts to probabilities."""
    probabilities = {}
    
    for from_state, transitions in transition_counts.items():
        total = sum(transitions.values())
        probabilities[from_state] = {
            to_state: count / total
            for to_state, count in transitions.items()
        }
    
    return probabilities

def print_probability_matrix(probabilities, all_states):
    """Print a formatted probability matrix."""
    print("\n" + "="*70)
    print("MARKOV CHAIN TRANSITION PROBABILITY MATRIX")
    print("="*70)
    
    states = sorted(all_states)
    col_w  = 16
    
    # Header
    print(f"{'FROM \\ TO':<20}", end="")
    for s in states:
        print(f"{s[:col_w]:<{col_w}}", end="")
    print()
    print("-" * (20 + col_w * len(states)))
    
    # Rows
    for from_state in states:
        print(f"{from_state:<20}", end="")
        for to_state in states:
            prob = probabilities.get(from_state, {}).get(to_state, 0.0)
            print(f"{prob:<{col_w}.4f}", end="")
        print()
    
    print("="*70)

def main():
    filepath = sys.argv[1] if len(sys.argv) > 1 else 'freeplane_events.log'
    
    print(f"[INFO] Parsing log file: {filepath}")
    user_sequences = parse_logs(filepath)
    
    all_states = set()
    all_probs  = {}
    
    for user, sequence in user_sequences.items():
        print(f"\n[USER] {user}  |  Total events: {len(sequence)}")
        
        counts = compute_transition_counts(sequence)
        probs  = compute_probabilities(counts)
        all_probs[user] = probs
        
        for s in sequence:
            all_states.add(s)
        
        print_probability_matrix(probs, all_states)
        
        print(f"\n[TRANSITIONS for {user}]")
        for from_state, transitions in sorted(probs.items()):
            for to_state, prob in sorted(transitions.items()):
                print(f"  P({from_state} -> {to_state}) = {prob:.4f} ({prob*100:.1f}%)")
    
    return all_probs

if __name__ == '__main__':
    main()