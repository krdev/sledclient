#!/usr/bin/env bash
if [  == 'master' ] && [  == 'false' ]; then
	openssl aes-256-cbc -K $encrypted_e591eb06ec02_key -iv $encrypted_e591eb06ec02_iv -in cd/codesigning.asc.enc -out cd/codesigning.asc -d
    gpg --fast-import cd/codesigning.asc
fi
