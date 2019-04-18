package controllers

import org.http4s.dsl.Http4sDsl

trait F[F[_]] extends Http4sDsl[F]
