# logwatcher
a simple Java program to watch logs and post new entries to Dynatrace API

$env:DynatraceUrl='https://identifier.live.dynatrace.com/api/v2/logs/ingest'

$env:DynatraceToken='dt0c01.stuff.more-stuff'

java -cp ".\logwatcher.jar" gov.hibc.logwatcher.App https://identifier.live.dynatrace.com/api/v2/logs/ingest dt0c01.stuff.morestuff ../test.log,fake.log

    // input args can be 1 of the following 3 options:
    // - just the word "test" (requires env vars DynatraceUrl and DynatraceToken)
    // - a single argument which is a comma separated list of logs (requires env vars DynatraceUrl and DynatraceToken)
    // - 3 arguments
    //      0) dynatrace url
    //      1) dynatrace token
    //      2) list of logs
