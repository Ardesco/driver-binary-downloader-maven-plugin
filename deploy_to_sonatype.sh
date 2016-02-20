#!/usr/bin/env bash
if [[ "${TRAVIS_BRANCH}" == 'master' ]] ; then
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
    mvn deploy -s ./target/maven-settings.xml
fi