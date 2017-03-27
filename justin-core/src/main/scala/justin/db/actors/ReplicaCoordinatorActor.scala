package justin.db.actors

import akka.actor.{Actor, Props}
import justin.db.actors.protocol.{ReadData, WriteData}
import justin.db.replica.{ReplicaReadCoordinator, ReplicaWriteCoordinator}

import scala.concurrent.ExecutionContext

class ReplicaCoordinatorActor(readCoordinator: ReplicaReadCoordinator, writeCoordinator: ReplicaWriteCoordinator) extends Actor {

  private implicit val ec: ExecutionContext = context.dispatcher

  override def receive: Receive = {
    case rd: ReadData  => readCoordinator.apply(rd.cmd, rd.clusterMembers).foreach(rd.sender ! _)
    case wd: WriteData => writeCoordinator.apply(wd.cmd, wd.clusterMembers).foreach(wd.sender ! _)
  }
}

object ReplicaCoordinatorActor {

  def props(readCoordinator: ReplicaReadCoordinator, writeCoordinator: ReplicaWriteCoordinator): Props = {
    Props(new ReplicaCoordinatorActor(readCoordinator, writeCoordinator))
  }
}
