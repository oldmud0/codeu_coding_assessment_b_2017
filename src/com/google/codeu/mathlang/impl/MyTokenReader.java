// Copyright 2017 Google Inc.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//    http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.codeu.mathlang.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.codeu.mathlang.core.tokens.NameToken;
import com.google.codeu.mathlang.core.tokens.NumberToken;
import com.google.codeu.mathlang.core.tokens.StringToken;
import com.google.codeu.mathlang.core.tokens.SymbolToken;
import com.google.codeu.mathlang.core.tokens.Token;
import com.google.codeu.mathlang.parsing.TokenReader;

// MY TOKEN READER
//
// This is YOUR implementation of the token reader interface. To know how
// it should work, read {@link com.google.codeu.mathlang.parsing.TokenReader}.
// You should not need to change any other files to get your token reader to
// work with the rest of the system.
public final class MyTokenReader implements TokenReader {

  private String source;
  private final List<TokenFactory<?>> tokenList = new ArrayList<>();

  public MyTokenReader(String source) {
    // warning, extreme lookahead sorcery, touch at your own risk
    tokenList.add(new TokenFactory<NameToken>("^[a-zA-Z]\\w*(?=(?:[^'\"]|[\"'][^'\"]*[\"'])*$)") {
      @Override
      public NameToken createToken(String object) {
        return new NameToken(object);
      }
    });
    tokenList.add(new TokenFactory<StringToken>("^\\\".+?[^\\\\]\\\"") {
      @Override
      public StringToken createToken(String object) {
        return new StringToken(object.substring(object.indexOf('"') + 1, object.lastIndexOf('"')));
      }
    });
    tokenList.add(new TokenFactory<NumberToken>("^\\d+") {
      @Override
      public NumberToken createToken(String object) {
        return new NumberToken(Double.parseDouble(object));
      }
    });
    tokenList.add(new TokenFactory<SymbolToken>("^[^\\w\\s]") {
      @Override
      public SymbolToken createToken(String object) {
        return new SymbolToken(object.charAt(0));
      }
    });
    this.source = source;
  }

  // Most of your work will take place here. For every call to |next| you should
  // return a token until you reach the end. When there are no more tokens, you
  // should return |null| to signal the end of input.
  // If for any reason you detect an error in the input, you may throw an
  // IOException which will stop all execution.
  @Override
  public Token next() throws IOException {
    try {
      for (TokenFactory<?> factory : tokenList) {
        Matcher tokenMatcher = factory.getMatcher(source);
        if (tokenMatcher.find()) {
//        System.out.printf("%s: %s%n", factory.getClass(), tokenMatcher.group());
//        System.out.println(source);
          source = source.substring(tokenMatcher.end()).trim();
//        System.out.println(source);
          return factory.createToken(tokenMatcher.group());
        }
      }
      return null;
    } catch (Exception e) {
      throw new IOException(e);
    }
  }

  private static abstract class TokenFactory<T extends Token> {
    /** Holds a regex pattern that corresponds to the format of the token. */
    private Pattern pattern;

    public TokenFactory(String pattern) {
      this.pattern = Pattern.compile(pattern);
    }

    public Matcher getMatcher(String text) {
      return pattern.matcher(text);
    }

    public abstract T createToken(String object);

  }
}
