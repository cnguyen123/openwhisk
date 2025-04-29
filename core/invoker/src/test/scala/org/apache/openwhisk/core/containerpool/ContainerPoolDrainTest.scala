package org.apache.openwhisk.core.containerpool

import akka.actor.ActorRef
import java.time.Instant
import org.apache.openwhisk.core.entity._
import org.apache.openwhisk.core.entity.ExecManifest
import org.apache.openwhisk.core.containerpool.test.ContainerPoolTestHelper
import org.apache.openwhisk.core.test.WhiskActorTest
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpecLike

class ContainerPoolDrainTest
    extends WhiskActorTest
    with AnyWordSpecLike
    with Matchers
    with ContainerPoolTestHelper {

  behavior of "ContainerPool with Drained Containers"

  it should "skip drained containers when scheduling" in {
    val invocationNamespace = EntityName("testns")
    val action = ExecutableWhiskActionMetaData(
      EntityPath("testns"),
      EntityName("testaction"),
      ExecManifest.nodejsDefault)

    val containerData = WarmedData(
      container = null,
      invocationNamespace = invocationNamespace,
      action = action,
      lastUsed = Instant.now,
      activeActivationCount = 0,
      resumeRun = None,
      isDrained = true)

    val idles = Map.empty[ActorRef, ContainerData] + (TestProbe().ref -> containerData)

    val scheduled = ContainerPool.schedule(action, invocationNamespace, idles)
    scheduled shouldBe empty
  }
}

