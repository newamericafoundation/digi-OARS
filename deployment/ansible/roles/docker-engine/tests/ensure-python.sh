#!/bin/sh -eu
# Make sure Python and its libraries are present before Ansible is used.

if command -v apt-get 2>/dev/null
then
    # Ubuntu has an awkward habit to drop Python
    command -v python || (sudo DEBIAN_FRONTEND=noninteractive apt-get -yqq update && sudo DEBIAN_FRONTEND=noninteractive apt-get -yqq install python-minimal aptitude python-apt)
fi

if command -v yum 2>/dev/null
then
    command -v python || yum install -y python python-apt
    if selinuxenabled
    then
      yum install -y libselinux-python
    fi
fi
