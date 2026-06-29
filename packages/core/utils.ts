export const isNotNull = <T>(val: T | null | undefined): val is T => val !== null && val !== undefined;
