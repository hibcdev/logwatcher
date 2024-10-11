# logwatcher
a simple Java program to watch logs and post new entries to Dynatrace API

$env:DynatraceUrl='https://identifier.live.dynatrace.com/api/v2/logs/ingest'
$env:DynatraceToken='dt0c01.stuff.more-stuff'
$env:LogFiles='../test1.log,../test2.log'

$env:Testing='testing'

# make sure the run script is executable
git update-index --chmod=+x .\.s2i\run

java -cp ".\logwatcher.jar" gov.hibc.logwatcher.App 

Oct. 8 current path is /tlp/tlp1/logs/teleplan-web-38-jlwbf

From the console (console-4-d8vqk) the latest teleplan-web is mounted, in this case jlwbf.

Logs on T2 look like this /tlp/tlp2/logs:
    archived/  teleplan-t2-45-cmwqz/  teleplan-t2-45-g4469/  teleplan-t2-45-l2wgg/

# works:
// old java -cp ".\logwatcher.jar" gov.hibc.logwatcher.LogFileFinder /tlp/tlp2/logs
java -cp ".\logwatcher.jar" gov.hibc.logwatcher.LogFileFinder /tlp/tlp2/logs glob:**/Broker*.log

 ls -alt
total 94724
-rw-r-----. 1 1000670000 root   590873 Oct  8  2024 tlp-out.log
-rw-r-----. 1 1000670000 root 33229373 Oct  8  2024 access.log
-rw-r-----. 1 1000670000 root 62748306 Oct  8  2024 BrokerTransaction.log
-rw-r-----. 1 1000670000 root     6218 Oct  8 14:18 catalina.log
-rw-r-----. 1 1000670000 root     6347 Oct  8 00:00 tlp-broker.log
-rw-r-----. 1 1000670000 root      442 Oct  8 00:00 localhost.log
-rw-r-----. 1 1000670000 root        0 Oct  8 00:00 host-manager.log
-rw-r-----. 1 1000670000 root        0 Oct  8 00:00 manager.log