#!/bin/bash

set -e

echo "=== Slurm test job ==="
echo "Job ID:       $SLURM_JOB_ID"
echo "Job name:     $SLURM_JOB_NAME"
echo "Node:         $(hostname)"
echo "User:         $(whoami)"
echo "Start time:   $(date)"
echo "CPUs:         $SLURM_CPUS_PER_TASK"
echo "Tasks:        $SLURM_NTASKS"
echo

echo "Sleeping for 10 seconds..."
sleep 10

echo
echo "End time:     $(date)"
echo "Job finished successfully"