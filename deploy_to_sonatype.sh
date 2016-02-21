#!/usr/bin/env bash
echo "branch is ${TRAVIS_BRANCH}"
if [[ "${TRAVIS_BRANCH}" == 'master' ]] ; then
    echo "Generating settings.xml"
    mkdir -p ./target
    MVNSETTINGS='
    <settings>
        <servers>
            <server>
                <id>sonatype-nexus-snapshots</id>
                <username>'"${SONATYPE_USER}"'</username>
                <password>'"${SONATYPE_PASS}"'</password>
            </server>
        </servers>
    </settings>'
    echo ${MVNSETTINGS} > ./target/maven-settings.xml
    echo "Deploying snapshot"
    mvn deploy -s ./target/maven-settings.xml
    exit $?
else
    echo "Not on master, skipping snapshot deployment"
fi