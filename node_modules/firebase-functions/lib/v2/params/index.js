"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
exports.defineJSON = exports.defineList = exports.defineFloat = exports.defineInt = exports.defineBoolean = exports.defineString = exports.declaredParams = void 0;
/** @hidden */
const types_1 = require("./types");
exports.declaredParams = [];
/**
 * Use a helper to manage the list such that params are uniquely
 * registered once only but order is preserved.
 * @internal
 */
function registerParam(param) {
    for (let i = 0; i < exports.declaredParams.length; i++) {
        if (exports.declaredParams[i].name === param.name) {
            exports.declaredParams.splice(i, 1);
        }
    }
    exports.declaredParams.push(param);
}
/**
 * Declare a string param.
 *
 * @param name The name of the environment variable to use to load the param.
 * @param options Configuration options for the param.
 * @returns A Param with a `string` return type for `.value`.
 */
function defineString(name, options = {}) {
    const param = new types_1.StringParam(name, options);
    registerParam(param);
    return param;
}
exports.defineString = defineString;
/**
 * Declare a boolean param.
 *
 * @param name The name of the environment variable to use to load the param.
 * @param options Configuration options for the param.
 * @returns A Param with a `boolean` return type for `.value`.
 */
function defineBoolean(name, options = {}) {
    const param = new types_1.BooleanParam(name, options);
    registerParam(param);
    return param;
}
exports.defineBoolean = defineBoolean;
/**
 * Declare an integer param.
 *
 * @param name The name of the environment variable to use to load the param.
 * @param options Configuration options for the param.
 * @returns A Param with a `number` return type for `.value`.
 */
function defineInt(name, options = {}) {
    const param = new types_1.IntParam(name, options);
    registerParam(param);
    return param;
}
exports.defineInt = defineInt;
/**
 * Declare a float param.
 *
 * @param name The name of the environment variable to use to load the param.
 * @param options Configuration options for the param.
 * @returns A Param with a `number` return type for `.value`.
 */
function defineFloat(name, options = {}) {
    const param = new types_1.FloatParam(name, options);
    registerParam(param);
    return param;
}
exports.defineFloat = defineFloat;
/**
 * Declare a list param (array of strings).
 *
 * @param name The name of the environment variable to use to load the param.
 * @param options Configuration options for the param.
 * @returns A Param with a `string[]` return type for `.value`.
 */
function defineList(name, options = {}) {
    const param = new types_1.ListParam(name, options);
    registerParam(param);
    return param;
}
exports.defineList = defineList;
/**
 * Declare a JSON param. The associated environment variable will be treated
 * as a JSON string when loading its value.
 *
 * @param name The name of the environment variable to use to load the param.
 * @param options Configuration options for the param.
 * @returns A Param with a specifiable return type for `.value`.
 */
function defineJSON(name, options = {}) {
    const param = new types_1.JSONParam(name, options);
    registerParam(param);
    return param;
}
exports.defineJSON = defineJSON;
