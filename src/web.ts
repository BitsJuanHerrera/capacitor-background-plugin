import { WebPlugin } from '@capacitor/core';

import type { BackgroundModePlugin, ISettings } from './definitions';

export class BackgroundModeWeb extends WebPlugin implements BackgroundModePlugin {
  async enable(): Promise<void> {
    throw this.unimplemented('Not implemented on web.');
  }

  async disable(): Promise<void> {
    throw this.unimplemented('Not implemented on web.');
  }

  async getSettings(): Promise<{ settings: ISettings }> {
    throw this.unimplemented('Not implemented on web.');
  }

  async setSettings(_settings: Partial<ISettings>): Promise<void> {
    throw this.unimplemented('Not implemented on web.');
  }

  async isActive(): Promise<{ active: boolean }> {
    throw this.unimplemented('Not implemented on web.');
  }

  async echo(options: { value: string }): Promise<{ value: string }> {
    console.log('ECHO 09:52 :>>>> ', options);
    return options;
  }
}
