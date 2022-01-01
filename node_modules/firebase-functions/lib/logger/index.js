"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
exports.error = exports.warn = exports.info = exports.log = exports.debug = exports.write = void 0;
const util_1 = require("util");
const common_1 = require("./common");
/** @internal */
function removeCircular(obj, refs = []) {
    if (typeof obj !== 'object' || !obj) {
        return obj;
    }
    // If the object defines its own toJSON, prefer that.
    if (obj.toJSON) {
        return obj.toJSON();
    }
    if (refs.includes(obj)) {
        return '[Circular]';
    }
    else {
        refs.push(obj);
    }
    let returnObj;
    if (Array.isArray(obj)) {
        returnObj = new Array(obj.length);
    }
    else {
        returnObj = {};
    }
    for (const k in obj) {
        if (refs.includes(obj[k])) {
            returnObj[k] = '[Circular]';
        }
        else {
            returnObj[k] = removeCircular(obj[k], refs);
        }
    }
    return returnObj;
}
/**
 * Writes a `LogEntry` to `stdout`/`stderr` (depending on severity).
 * @param entry The `LogEntry` including severity, message, and any additional structured metadata.
 */
function write(entry) {
    if (common_1.SUPPORTS_STRUCTURED_LOGS) {
        common_1.UNPATCHED_CONSOLE[common_1.CONSOLE_SEVERITY[entry.severity]](JSON.stringify(removeCircular(entry)));
        return;
    }
    let message = entry.message || '';
    const jsonPayload = {};
    let jsonKeyCount = 0;
    for (const k in entry) {
        if (!['severity', 'message'].includes(k)) {
            jsonKeyCount++;
            jsonPayload[k] = entry[k];
        }
    }
    if (jsonKeyCount > 0) {
        message = `${message} ${JSON.stringify(removeCircular(jsonPayload), null, 2)}`;
    }
    common_1.UNPATCHED_CONSOLE[common_1.CONSOLE_SEVERITY[entry.severity]](message);
}
exports.write = write;
/**
 * Writes a `DEBUG` severity log. If the last argument provided is a plain object,
 * it is added to the `jsonPayload` in the Cloud Logging entry.
 * @param args Arguments, concatenated into the log message with space separators.
 */
function debug(...args) {
    write(entryFromArgs('DEBUG', args));
}
exports.debug = debug;
/**
 * Writes an `INFO` severity log. If the last argument provided is a plain object,
 * it is added to the `jsonPayload` in the Cloud Logging entry.
 * @param args Arguments, concatenated into the log message with space separators.
 */
function log(...args) {
    write(entryFromArgs('INFO', args));
}
exports.log = log;
/**
 * Writes an `INFO` severity log. If the last argument provided is a plain object,
 * it is added to the `jsonPayload` in the Cloud Logging entry.
 * @param args Arguments, concatenated into the log message with space separators.
 */
function info(...args) {
    write(entryFromArgs('INFO', args));
}
exports.info = info;
/**
 * Writes a `WARNING` severity log. If the last argument provided is a plain object,
 * it is added to the `jsonPayload` in the Cloud Logging entry.
 * @param args Arguments, concatenated into the log message with space separators.
 */
function warn(...args) {
    write(entryFromArgs('WARNING', args));
}
exports.warn = warn;
/**
 * Writes an `ERROR` severity log. If the last argument provided is a plain object,
 * it is added to the `jsonPayload` in the Cloud Logging entry.
 * @param args Arguments, concatenated into the log message with space separators.
 */
function error(...args) {
    write(entryFromArgs('ERROR', args));
}
exports.error = error;
/** @hidden */
function entryFromArgs(severity, args) {
    let entry = {};
    const lastArg = args[args.length - 1];
    if (lastArg && typeof lastArg == 'object' && lastArg.constructor == Object) {
        entry = args.pop();
    }
    return Object.assign({}, entry, {
        severity,
        // mimic `console.*` behavior, see https://nodejs.org/api/console.html#console_console_log_data_args
        message: util_1.format.apply(null, args),
    });
}
