// React类型声明文件
declare module 'react' {
  export interface Component<P = {}, S = {}> {
    render(): ReactElement | null
  }
  
  export interface FunctionComponent<P = {}> {
    (props: P): ReactElement | null
  }
  
  export type FC<P = {}> = FunctionComponent<P>
  
  export interface ReactElement<P = any, T extends string | JSXElementConstructor<any> = string | JSXElementConstructor<any>> {
    type: T
    props: P
    key: Key | null
  }
  
  export type ReactNode = ReactElement | string | number | boolean | null | undefined
  
  export interface JSXElementConstructor<P> {
    (props: P): ReactElement | null
  }
  
  export type Key = string | number
  
  export function useState<S>(initialState: S | (() => S)): [S, (value: S | ((prevState: S) => S)) => void]
  export function useState<S = undefined>(): [S | undefined, (value: S | ((prevState: S | undefined) => S | undefined)) => void]
  
  export function useEffect(effect: () => void | (() => void), deps?: any[]): void
  export function useCallback<T extends (...args: any[]) => any>(callback: T, deps: any[]): T
  export function useMemo<T>(factory: () => T, deps: any[]): T
  export function useRef<T>(initialValue: T): { current: T }
  
  export const createElement: any
  export const Fragment: any
}

declare global {
  namespace JSX {
    interface IntrinsicElements {
      [elemName: string]: any
    }
  }
}

declare module 'react/jsx-runtime' {
  export const jsx: any
  export const jsxs: any
  export const Fragment: any
}
