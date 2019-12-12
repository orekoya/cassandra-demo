#!/bin/sh
# wait-for-DSE.sh

set -e

host="$1"
port="$2"
shift
shift
cmd="$@"

until cqlsh "$host" "$port" -e 'DESC FULL SCHEMA'; do
  >&2 echo "DSE is unavailable - sleeping"
  sleep 1
done

>&2 echo "DSE is up - executing command"
exec $cmd
