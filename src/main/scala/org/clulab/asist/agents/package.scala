package org.clulab.asist

import org.json4s.NoTypeHints
import org.json4s.jackson.Serialization

package object agents {
  // Used so Json serializers can recognize case classes
  implicit val formats = Serialization.formats(NoTypeHints)
}
