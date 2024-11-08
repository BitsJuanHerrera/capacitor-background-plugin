export interface ISettings {
  title: string;
  text: string;
  icon: string;
  channelName: string;
  channelDescription: string;
  showWhen: boolean;
}
export interface BackgroundModePlugin {
  requestNotificationPermission(): Promise<void>;
  enable(): Promise<void>;
  disable(): Promise<void>;
  getSettings(): Promise<{ settings: ISettings }>;
  setSettings(settings: Partial<ISettings>): Promise<void>;
  isActive(): Promise<{ active: boolean }>;
  echo(options: { value: string }): Promise<{ value: string }>;
}
