#! /usr/bin/env python3

import pandas as pd
import random

random.seed(12345)

A = [random.randrange(1, 1001, 1) for _ in range(1000)]
B = [random.randrange(1, 1001, 1) for _ in range(1000)]
C = [random.randrange(1, 1001, 1) for _ in range(1000)]

df = pd.DataFrame({"A": A, "B": B, "C": C})
df.to_csv("data", index=False, header=False)
