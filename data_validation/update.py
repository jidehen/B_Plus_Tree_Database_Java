#! /usr/bin/env python3

import pandas as pd
df = pd.read_csv("data", header=None);
ids = (df[0] >= 500) & (df[0] <= 600)
df.loc[ids,1] = 2000
df.to_csv("expected/update", header=None, index=False);

