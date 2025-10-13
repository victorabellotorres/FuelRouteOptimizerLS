import csv
import sys
from collections import defaultdict
import math

if len(sys.argv) < 2:
    print("Usage: analyze_results.py <csvfile>")
    sys.exit(1)

csvfile = sys.argv[1]

# fields: seed,iteration,initial,algorithm,timeMs,initialHeur,finalHeur,assigned,nodesExpanded
groups = defaultdict(list)

with open(csvfile, newline='', encoding='utf-8') as f:
    reader = csv.DictReader(f)
    for row in reader:
        key = (int(row['initial']), int(row['algorithm']))
        try:
            final = float(row['finalHeur']) if row['finalHeur']!='NA' else None
        except:
            final = None
        try:
            initial = float(row['initialHeur']) if row['initialHeur']!='NA' else None
        except:
            initial = None
        try:
            time = float(row['timeMs'])
        except:
            time = None
        try:
            nodes = int(row['nodesExpanded']) if row['nodesExpanded']!='NA' else None
        except:
            nodes = None
        assigned = row.get('assigned','NA')
        groups[key].append({'final':final,'initial':initial,'time':time,'nodes':nodes,'assigned':assigned,'row':row})

# compute stats
print(f"Analysis of {csvfile}\n")
print("initial,algorithm,count,mean_initial,sd_initial,mean_final,sd_final,mean_time_ms,sd_time_ms,mean_nodes,sd_nodes,mean_assigned")
for key in sorted(groups.keys()):
    items = groups[key]
    count = len(items)
    initials = [x['initial'] for x in items if x['initial'] is not None]
    finals = [x['final'] for x in items if x['final'] is not None]
    times = [x['time'] for x in items if x['time'] is not None]
    nodes = [x['nodes'] for x in items if x['nodes'] is not None]
    assigned_nums = []
    for x in items:
        a = x['assigned']
        if a and '/' in a:
            try:
                assigned_nums.append(int(a.split('/')[0]))
            except:
                pass
    def mean_sd(arr):
        if not arr: return ('NA','NA')
        m = sum(arr)/len(arr)
        sd = math.sqrt(sum((v-m)**2 for v in arr)/len(arr))
        return (round(m,3), round(sd,3))
    mi,si = mean_sd(initials)
    mf,sf = mean_sd(finals)
    mt,st = mean_sd(times)
    mn,sn = mean_sd(nodes)
    ma,sa = mean_sd(assigned_nums)
    print(f"{key[0]},{key[1]},{count},{mi},{si},{mf},{sf},{mt},{st},{mn},{sn},{ma},{sa}")

print('\nRaw groups sizes:')
for k in sorted(groups.keys()):
    print(k, len(groups[k]))
