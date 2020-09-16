Docker Engine
=============

Install Docker CE engine.

* This role has been tested with Virtualbox.
* This role has been tested with Ubuntu 18.04 LTS (Bionic) and Ubuntu 16.04 LTS (Xenial).
* This role has been tested with CentOS 7.

Requirements
------------

None

Role Variables
--------------

#### `docker_engine_package_name`

The name of a package which contains the service to install by this role.

Default is: `docker-ce`

#### `docker_engine_package_version`

The version of the package to install.
One of `latest`, `present` or package version.

Default is: `present`

#### `docker_engine_service_name`

The name of the service once installed from the package.

Default is: `docker`

#### `docker_engine_gpg_key_id`

The GPG key id for the Debian/Ubuntu packages.

Default is: `9DC858229FC7DD38854AE2D88D81803C0EBFCD88`

#### `docker_engine_gpg_keyserver`

The GPG keyserver to fetch GPG key from.

Default is: `hkp://p80.pool.sks-keyservers.net:80`

#### `docker_engine_docker_users`

List of users to have an access to the local docker engine.
For example:
- '{{ ansible_env.USER }}'
- operator

Default is: `[]`

#### `docker_engine_conf`

The configuration for Docker engine.
Conveniently the configuration file is a JSON format, so this variable is 1:1 mapping
See [Docker engine doc](https://docs.docker.com/engine/reference/commandline/dockerd/#daemon-configuration-file)
to check what is accepted here

Default is: `{}`

_(list extracted from [defaults/main.yml](./defaults/main.yml))_

Dependencies
------------

None

Example Playbook
----------------

Here is an example playbook for this role:

    - hosts: all
      gather_facts: True
      become: False
      roles:
        - role: ansible-r3-roles/docker-engine
          docker_engine_package_version: 'latest'

see [tests.yml](./tests/test.yml) for another example, used for testing this role.

Testing role with Vagrant
-------------------------

Please run a Shell script - this requires [Ansible](http://docs.ansible.com) (at least 2.0) and [Vagrant](http://www.vagrantup.com/docs) installed:
> `./run-test`
