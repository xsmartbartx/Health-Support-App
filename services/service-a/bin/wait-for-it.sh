#!/usr/bin/env bash
# wait-for-it.sh: wait for a TCP host:port to become available before executing a command.
# Usage: wait-for-it.sh host:port [-t timeout] [-- command args...]

WAITFORIT_cmdname=${0##*/}

echoerr() { if [[ $WAITFORIT_QUIET -ne 1 ]]; then echo "$@" 1>&2; fi }

usage() {
    cat << USAGE >&2
Usage:
    $WAITFORIT_cmdname host:port [-s] [-t timeout] [-- command args]
    -h HOST | --host=HOST       Host or IP under test
    -p PORT | --port=PORT       TCP port under test
                                 Alternatively, you specify the host and port as host:port
    -s | --strict               Only execute subcommand if the test succeeds
    -q | --quiet                Don't output any status messages
    -t TIMEOUT | --timeout=TIMEOUT
                                 Timeout in seconds, zero for no timeout
    -- COMMAND ARGS             Execute command with args after the test finishes
USAGE
    exit 1
}

wait_for() {
    if [[ $WAITFORIT_TIMEOUT -gt 0 ]]; then
        echoerr "$WAITFORIT_cmdname: waiting $WAITFORIT_TIMEOUT seconds for $WAITFORIT_HOST:$WAITFORIT_PORT"
    else
        echoerr "$WAITFORIT_cmdname: waiting for $WAITFORIT_HOST:$WAITFORIT_PORT without a timeout"
    fi
    WAITFORIT_start_ts=$(date +%s)
    while :
    do
        if [[ $WAITFORIT_ISBUSY -eq 1 ]]; then
            nc -z "$WAITFORIT_HOST" "$WAITFORIT_PORT"
            WAITFORIT_result=$?
        else
            (exec 3<>"/dev/tcp/$WAITFORIT_HOST/$WAITFORIT_PORT") 2>/dev/null
            WAITFORIT_result=$?
        fi
        if [[ $WAITFORIT_result -eq 0 ]]; then
            WAITFORIT_end_ts=$(date +%s)
            echoerr "$WAITFORIT_cmdname: $WAITFORIT_HOST:$WAITFORIT_PORT is available after $((WAITFORIT_end_ts - WAITFORIT_start_ts)) seconds"
            break
        fi
        sleep 1
        if [[ $WAITFORIT_TIMEOUT -gt 0 && $(($(date +%s) - WAITFORIT_start_ts)) -ge $WAITFORIT_TIMEOUT ]]; then
            echoerr "$WAITFORIT_cmdname: timeout occurred after waiting $WAITFORIT_TIMEOUT seconds for $WAITFORIT_HOST:$WAITFORIT_PORT"
            exit 1
        fi
    done
    return 0
}

WAITFORIT_TIMEOUT=15
WAITFORIT_STRICT=0
WAITFORIT_QUIET=0
WAITFORIT_HOST=
WAITFORIT_PORT=

while [[ $# -gt 0 ]]
do
    case "$1" in
        *:* )
        WAITFORIT_HOST=$(printf "%s\n" "$1"| cut -d : -f 1)
        WAITFORIT_PORT=$(printf "%s\n" "$1"| cut -d : -f 2)
        shift 1
        ;;
        -h)
        WAITFORIT_HOST="$2"
        shift 2
        ;;
        --host=*)
        WAITFORIT_HOST="${1#*=}"
        shift 1
        ;;
        -p)
        WAITFORIT_PORT="$2"
        shift 2
        ;;
        --port=*)
        WAITFORIT_PORT="${1#*=}"
        shift 1
        ;;
        -t)
        WAITFORIT_TIMEOUT="$2"
        shift 2
        ;;
        --timeout=*)
        WAITFORIT_TIMEOUT="${1#*=}"
        shift 1
        ;;
        -s | --strict)
        WAITFORIT_STRICT=1
        shift 1
        ;;
        -q | --quiet)
        WAITFORIT_QUIET=1
        shift 1
        ;;
        --)
        shift
        WAITFORIT_CLI=("$@")
        break
        ;;
        --help)
        usage
        ;;
        *)
        echoerr "Unknown argument: $1"
        usage
        ;;
    esac
done

if [[ "$WAITFORIT_HOST" == "" || "$WAITFORIT_PORT" == "" ]]; then
    echoerr "Error: you need to provide a host and port to test."
    usage
fi

WAITFORIT_ISBUSY=0
if command -v nc >/dev/null 2>&1; then
    WAITFORIT_ISBUSY=1
fi

wait_for

if [[ ${#WAITFORIT_CLI[@]} -gt 0 ]]; then
    if [[ $WAITFORIT_STRICT -eq 1 ]]; then
        echoerr "$WAITFORIT_cmdname: strict mode, sourcing target: $WAITFORIT_HOST:$WAITFORIT_PORT"
    fi
    exec "${WAITFORIT_CLI[@]}"
fi
exit 0
