/* eslint-disable no-template-curly-in-string */
import { config as dotenv } from 'dotenv'
import type { Configuration } from 'electron-builder'

dotenv()

export const config = {
  productName: 'Haze Client',
  appId: 'hazeclient',
  directories: {
    output: 'build/output',
    buildResources: 'build',
    app: '.',
  },
  protocols: {
    name: 'HazeClient',
    schemes: ['hazeclient'],
  },
  // assign publish for auto-updater
  // Haze Launcher GitHub repository
  publish: [{
    provider: 'github',
    owner: 'FaaizJohar',
    repo: 'HazeClient',
  }],
  files: [{
    from: 'dist',
    to: '.',
    filter: ['**/*.js', '**/*.ico', '**/*.png', '**/*.webp', '**/*.svg', '*.node', '**/*.html', '**/*.css', '**/*.woff2'],
  }, {
    from: '.',
    to: '.',
    filter: 'package.json',
  }],
  artifactName: 'hazeclient-${version}-${platform}-${arch}.${ext}',
  appx: {
    displayName: 'Haze Client',
    applicationId: 'hazeclient',
    identityName: 'hazeclient',
    backgroundColor: 'transparent',
    publisher: process.env.PUBLISHER,
    publisherDisplayName: 'Cavrix Core Technologies',
    setBuildNumber: true,
  },
  dmg: {
    artifactName: 'hazeclient-${version}-${arch}.${ext}',
    contents: [
      {
        x: 410,
        y: 150,
        type: 'link',
        path: '/Applications',
      },
      {
        x: 130,
        y: 150,
        type: 'file',
      },
    ],
  },
  mac: {
    icon: 'icons/dark.icns',
    darkModeSupport: true,
    target: [
      {
        target: 'dmg',
        arch: ['arm64', 'x64'],
      },
    ],
    extendInfo: {
      NSMicrophoneUsageDescription: 'A Minecraft mod wants to access your microphone.',
      NSCameraUsageDescription: 'Please give us access to your camera',
      'com.apple.security.device.audio-input': true,
      'com.apple.security.device.camera': true,
    },
  },
  win: {
    certificateFile: undefined as string | undefined,
    publisherName: 'Cavrix Core Technologies',
    icon: 'icons/dark.ico',
    target: [
      {
        target: 'zip',
        arch: [
          'x64'
        ],
      },
      {
        target: 'nsis',
        arch: ['x64']
      },
      'appx',
    ],
  },
  linux: {
    executableName: 'hazeclient',
    desktop: {
      MimeType: 'x-scheme-handler/hazeclient',
      StartupWMClass: 'hazeclient',
    },
    category: 'Game',
    icon: 'icons/dark.icns',
    artifactName: 'hazeclient-${version}-${arch}.${ext}',
    target: [
      { target: 'deb', arch: ['x64', 'arm64'] },
      { target: 'rpm', arch: ['x64', 'arm64'] },
      { target: 'AppImage', arch: ['x64', 'arm64'] },
      { target: 'tar.xz', arch: ['x64', 'arm64'] },
      { target: 'pacman', arch: ['x64', 'arm64'] },
    ],
  },
  snap: {
    publish: [
      'github',
    ],
  },
  nsis: {
    artifactName: 'HazeClient-Installer.exe',
    oneClick: false,
    perMachine: false
  }
} satisfies Configuration
