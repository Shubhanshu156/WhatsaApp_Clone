/** @hidden */
import { BooleanParam, FloatParam, IntParam, JSONParam, ListParam, Param, ParamOptions, StringParam } from './types';
export { ParamOptions };
export declare const declaredParams: Param[];
/**
 * Declare a string param.
 *
 * @param name The name of the environment variable to use to load the param.
 * @param options Configuration options for the param.
 * @returns A Param with a `string` return type for `.value`.
 */
export declare function defineString(name: string, options?: ParamOptions<string>): StringParam;
/**
 * Declare a boolean param.
 *
 * @param name The name of the environment variable to use to load the param.
 * @param options Configuration options for the param.
 * @returns A Param with a `boolean` return type for `.value`.
 */
export declare function defineBoolean(name: string, options?: ParamOptions<boolean>): BooleanParam;
/**
 * Declare an integer param.
 *
 * @param name The name of the environment variable to use to load the param.
 * @param options Configuration options for the param.
 * @returns A Param with a `number` return type for `.value`.
 */
export declare function defineInt(name: string, options?: ParamOptions<number>): IntParam;
/**
 * Declare a float param.
 *
 * @param name The name of the environment variable to use to load the param.
 * @param options Configuration options for the param.
 * @returns A Param with a `number` return type for `.value`.
 */
export declare function defineFloat(name: string, options?: ParamOptions<number>): FloatParam;
/**
 * Declare a list param (array of strings).
 *
 * @param name The name of the environment variable to use to load the param.
 * @param options Configuration options for the param.
 * @returns A Param with a `string[]` return type for `.value`.
 */
export declare function defineList(name: string, options?: ParamOptions<string[]>): ListParam;
/**
 * Declare a JSON param. The associated environment variable will be treated
 * as a JSON string when loading its value.
 *
 * @param name The name of the environment variable to use to load the param.
 * @param options Configuration options for the param.
 * @returns A Param with a specifiable return type for `.value`.
 */
export declare function defineJSON<T = any>(name: string, options?: ParamOptions<T>): JSONParam;
