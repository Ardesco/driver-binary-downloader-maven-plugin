selenium-standalone-server-plugin
=================================

A Maven plugin that will download the WebDriver stand alone server executables for use in your mavenised Selenium project.

Default Usage:

<plugins>
    <plugin>
        <groupId>com.lazerycode.selenium</groupId>
        <artifactId>driver-binary-downloader-maven-plugin</artifactId>
        <version>1.0.0-SNAPSHOT</version>
        <executions>
            <execution>
                <goals>
                    <goal>selenium</goal>
                </goals>
            </execution>
        </executions>
    </plugin>
</plugins>