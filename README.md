# capacitor-plugin-background

Este plugin de Capacitor permite la ejecución de tareas de **AUDIO** en segundo plano en aplicaciones móviles **ANDROID** (Versión API34), facilitando la integración de servicios nativos que funcionan incluso cuando la aplicación no está en primer plano. Incluye características como la gestión de notificaciones personalizadas, el control del estado de la aplicación y la optimización del rendimiento de fondo, todo con una configuración sencilla y flexible.

## Install

```bash
npm install @juankmiloh/capacitor-plugin-background
yarn add @juankmiloh/capacitor-plugin-background
npx cap sync
```

## Publicar plugin

```bash
# 1. npm login
# 2. Cambiar version de plugin en el package para publicar en npm
# 3. npm run build
# 4. npm publish --access public
```

## API

<docgen-index>

* [`requestNotificationPermission()`](#requestnotificationpermission)
* [`enable()`](#enable)
* [`disable()`](#disable)
* [`getSettings()`](#getsettings)
* [`setSettings(...)`](#setsettings)
* [`isActive()`](#isactive)
* [`echo(...)`](#echo)
* [Interfaces](#interfaces)
* [Type Aliases](#type-aliases)

</docgen-index>

<docgen-api>
<!--Update the source file JSDoc comments and rerun docgen to update the docs below-->

### requestNotificationPermission()

```typescript
requestNotificationPermission() => Promise<void>
```

--------------------


### enable()

```typescript
enable() => Promise<void>
```

--------------------


### disable()

```typescript
disable() => Promise<void>
```

--------------------


### getSettings()

```typescript
getSettings() => Promise<{ settings: ISettings; }>
```

**Returns:** <code>Promise&lt;{ settings: <a href="#isettings">ISettings</a>; }&gt;</code>

--------------------


### setSettings(...)

```typescript
setSettings(settings: Partial<ISettings>) => Promise<void>
```

| Param          | Type                                                                                  |
| -------------- | ------------------------------------------------------------------------------------- |
| **`settings`** | <code><a href="#partial">Partial</a>&lt;<a href="#isettings">ISettings</a>&gt;</code> |

--------------------


### isActive()

```typescript
isActive() => Promise<{ active: boolean; }>
```

**Returns:** <code>Promise&lt;{ active: boolean; }&gt;</code>

--------------------


### echo(...)

```typescript
echo(options: { value: string; }) => Promise<{ value: string; }>
```

| Param         | Type                            |
| ------------- | ------------------------------- |
| **`options`** | <code>{ value: string; }</code> |

**Returns:** <code>Promise&lt;{ value: string; }&gt;</code>

--------------------


### Interfaces


#### ISettings

| Prop                     | Type                 |
| ------------------------ | -------------------- |
| **`title`**              | <code>string</code>  |
| **`text`**               | <code>string</code>  |
| **`icon`**               | <code>string</code>  |
| **`channelName`**        | <code>string</code>  |
| **`channelDescription`** | <code>string</code>  |
| **`showWhen`**           | <code>boolean</code> |


### Type Aliases


#### Partial

Make all properties in T optional

<code>{ [P in keyof T]?: T[P]; }</code>

</docgen-api>
