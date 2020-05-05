#! /usr/bin/env python3
import pandas as pd
df = pd.read_csv("data", header=None);
gt = (df[0] >= 500)
lt = (df[0] <= 600)

df.loc[gt & lt,:].to_csv("expected/between", header=None, index=False);
df.loc[lt,:].to_csv("expected/lt", header=None, index=False);
df.loc[gt,:].to_csv("expected/gt", header=None, index=False);

b = (df[1] >= 100) & (df[1] <= 200)
df.loc[b & gt & lt,:].to_csv("expected/composite", header=None, index=False);

