# logwatcher
a simple Java program to watch logs and post new entries to Dynatrace API

$env:DynatraceUrl='https://identifier.live.dynatrace.com/api/v2/logs/ingest'
$env:DynatraceToken='dt0c01.stuff.more-stuff'
$env:LogFiles='../test1.log,../test2.log'

$env:Testing='testing'

# make sure the run script is executable
git update-index --chmod=+x .\.s2i\run

java -cp ".\logwatcher.jar" gov.hibc.logwatcher.App 