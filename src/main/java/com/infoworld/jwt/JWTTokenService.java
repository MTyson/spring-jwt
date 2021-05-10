package com.infoworld.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Clock;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.impl.compression.GzipCompressionCodec;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Map;

import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableMap;

import static io.jsonwebtoken.SignatureAlgorithm.HS256;
import static io.jsonwebtoken.impl.TextCodec.BASE64;
import static java.util.Objects.requireNonNull;
import static org.apache.commons.lang3.StringUtils.substringBeforeLast;

@Service
//@FieldDefaults(level = PRIVATE, makeFinal = true)
final class JWTTokenService implements Clock, TokenService {
  private static final GzipCompressionCodec COMPRESSION_CODEC = new GzipCompressionCodec();

  String issuer;
  String secretKey; 

  JWTTokenService() {
    super();
    this.issuer = requireNonNull("infoworld");
    System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!! issuer " + issuer);
    this.secretKey = BASE64.encode("www.infoworld.com");
  }

  public String newToken(final Map<String, String> attributes) {
    final DateTime now = DateTime.now();
    final Claims claims = Jwts.claims().setIssuer(issuer).setIssuedAt(now.toDate());

    claims.putAll(attributes);

    return Jwts.builder().setClaims(claims).signWith(HS256, secretKey).compressWith(COMPRESSION_CODEC)
      .compact();
  }

  @Override
  public Map<String, String> verify(final String token) {
    final JwtParser parser = Jwts.parser().requireIssuer(issuer).setClock(this).setSigningKey(secretKey);
    return parseClaims(() -> parser.parseClaimsJws(token).getBody());
  }

  private static Map<String, String> parseClaims(final Supplier<Claims> toClaims) {
    try {
      final Claims claims = toClaims.get();
      final ImmutableMap.Builder<String, String> builder = ImmutableMap.builder();
      for (final Map.Entry<String, Object> e: claims.entrySet()) {
        builder.put(e.getKey(), String.valueOf(e.getValue()));
      }
      return builder.build();
    } catch (final IllegalArgumentException | JwtException e) {
      return ImmutableMap.of();
    }
  }

  @Override
  public Date now() {
    final DateTime now = DateTime.now();
    return now.toDate(); 
  }
}
