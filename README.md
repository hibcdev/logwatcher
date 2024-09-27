# logwatcher
a simple Java program to watch logs and post new entries to Dynatrace API

$env:DynatraceUrl='https://identifier.live.dynatrace.com/api/v2/logs/ingest'
$env:DynatraceToken='dt0c01.stuff.more-stuff'

java -cp ".\logwatcher.jar" gov.hibc.logwatcher.App https://identifier.live.dynatrace.com/api/v2/logs/ingest dt0c01.stuff.morestuff ../test.log,fake.log