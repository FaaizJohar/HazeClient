// Stubbed package
export const AnyError = class extends Error {};
export const isSystemError = () => false;
export const download = async () => {};
export const getDownloadBaseOptions = () => ({});
export const onDownloadSingle = () => {};
export const createMultiplayer = () => ({});
export const listen = () => {};
export const Tracker = {};
export const hashAndFiletypeResource = async () => ({});
export const hashResource = async () => '';
export const ResourceParser = class {};
export enum ResourceAction { Upsert, Remove, Update }
export enum ResourceErrorAction { Upsert, Remove }
export type ResourceActionTuple = any;
export type ResourceErrorActionTuple = any;
export type ResourceError = any;
export type UpdateResourcePayload = any;
export const isFabricResource = () => false;
export const isForgeResource = () => false;
export const isLiteloaderResource = () => false;
export const isQuiltResource = () => false;

export const ResourceDomain = {} as any;
export const getDomainedPath = {} as any;
export const getFile = {} as any;
export const isValidModrinthId = {} as any;
export const isValidCurseforgeRef = {} as any;
export const isValidModrinthRef = {} as any;
export const ResourceManager = {} as any;
export const migrate = {} as any;
export const sweepCorruptedRefs = {} as any;