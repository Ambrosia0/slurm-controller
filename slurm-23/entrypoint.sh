#!/bin/bash

echo -e "\nstarting db..."
systemctl start mariadb munge sshd

echo -e "\nwaiting for db to start..."
sleep 1

echo -e "\nstarting slurmdbd..."

systemctl start slurmdbd

echo -e "\nwaiting for slurmdbd to start..."
sleep 1

echo -e "\nstarting slurm..."
systemctl start slurmctld

echo -e "\nstarting slurmrestd manually..."
systemctl start slurmrestd

mkdir -p /sys/fs/cgroup/system.slice
echo -e "\nstarting compute nodes..."
systemctl start slurmd0 slurmd1 slurmd2 slurmd3 slurmd4

echo -e "\nCreating default account"
sacctmgr --immediate add account name=default cluster=mycluster

echo -e "\nCreating admin"
sacctmgr --immediate add user name=xenon account=default
sacctmgr --immediate modify user where name=xenon set adminlevel=admin

echo -e "\nAdmin created"

echo -e "\nCreating group"
groupadd webguiusers
usermod -aG webguiusers xenon

echo -e "\nEverything is started"

sleep infinity