#! /usr/bin/env bash
expected="data_validation/expected/"
results="data_validation/results/"
for filename in data_validation/expected/*; do
    fn=$(basename $filename);
    exp=$expected$fn;
    res=$results$fn;
    if diff <(sort $exp) <(sort $res.csv) > /dev/null; then
        echo OK: Results for $fn match
    else
        echo ERROR: Results for $fn dont match
    fi
done

