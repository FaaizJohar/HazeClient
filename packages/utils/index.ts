// Stubbed package
export class AnyError extends Error {
  static make(name: string) {
    return class extends AnyError {
      constructor(message?: string) {
        super(message);
        this.name = name;
      }
    };
  }
}
export const isSystemError = () => false;
export const download = async () => {};
export const getDownloadBaseOptions = () => ({});
export const onDownloadSingle = () => {};
export const createMultiplayer = () => ({});
export const listen = () => {};
export const Tracker = {};

export const WorkerQueue = {} as any;
export const AggregateExecutor = {} as any;