export interface BackgroundModePlugin {
  echo(options: { value: string }): Promise<{ value: string }>;
  activate(): Promise<void>;
  deactivate(): Promise<void>;
  isActive(): Promise<{ active: boolean }>;
  requestAutoStartPermission(): Promise<{ tipo_celular: string }>;
}
