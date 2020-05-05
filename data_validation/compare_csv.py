#! /usr/bin/env python3
import pandas as pd

def read_sort(f):
    df = pd.read_csv(f, header=None)
    cols = list(df.columns)
    cols.sort()
    return df.sort_values(by=cols).reset_index(drop=True)

for filename in ["lt", "gt", "delete", "between", "composite", "update"]:
    exp = "expected/{}".format(filename)
    res = "results/{}.csv".format(filename)

    df1 = read_sort(exp)
    df2 = read_sort(res)
    pd.testing.assert_frame_equal(df1, df2)
    print("Check {}: OK!".format(filename))

