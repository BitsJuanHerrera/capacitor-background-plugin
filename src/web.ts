import { WebPlugin } from '@capacitor/core';

import type { BackgroundModePlugin } from './definitions';

export class BackgroundModeWeb extends WebPlugin implements BackgroundModePlugin {
  async requestAutoStartPermission(): Promise<{ tipo_celular: string }> {
    console.log('Activar el AUTOSTART on web (mock)');
    return { tipo_celular: 'none' };
  }

  async activate(): Promise<void> {
    console.log('Background mode activated on web (mock)');
  }

  async deactivate(): Promise<void> {
    console.log('Background mode deactivated on web (mock)');
  }

  async isActive(): Promise<{ active: boolean }> {
    console.log('Checking background mode status on web (mock)');
    return { active: false };
  }

  async echo(options: { value: string }): Promise<{ value: string }> {
    console.log('ECHO 8:05 :>>>> ', options);
    return options;
  }
}
