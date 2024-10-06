package com.fgiannesini.web;

import com.fgiannesini.Matching;

public record TranslationResponseDto(Matching matching, String realTranslation) {
}
