#!/bin/bash
# $1 - base version
# $2 - head version
if dpkg --compare-versions "$1" "gt" "$2"; then
    echo "-1"
elif dpkg --compare-versions "$1" "lt" "$2"; then
    echo "1"
else
    echo "0"
fi