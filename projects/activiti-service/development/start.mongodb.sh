#!/bin/sh

mkdir target
mkdir target/db
mongod -dbpath target/db
