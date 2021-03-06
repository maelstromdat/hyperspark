package pfsp.algorithms

import Array._
import it.polimi.hyperh._
import it.polimi.hyperh.types.Types._
import it.polimi.hyperh.solution.EvaluatedSolution
import it.polimi.hyperh.problem.Problem
import it.polimi.hyperh.solution.Solution
import it.polimi.hyperh.algorithms.Algorithm
import pfsp.problem.PfsProblem
import pfsp.util.PermutationUtility
import it.polimi.hyperh.spark.StoppingCondition

/**
 * @author Nemanja
 */
/*class NEHAlgorithm() extends Serializable {
  
}*/

//Problem Factory
class NEHAlgorithm() extends Algorithm {
  
  def constructSolution(p:PfsProblem, partialSolution: List[Int], remainingJobs: List[Int]): EvaluatedSolution = {
      //STEP 2.2 get best permutation of two jobs
      var bestPermutation = PermutationUtility.getBestPermutation(PermutationUtility.generatePermutations(partialSolution), p)
      //STEP 3 of NEH algorithm
      //from 0 until numOfRemainingJobs (in NEH this is marked as for k=3 to numOfJobs)
      for (i <- 0 until remainingJobs.size) {
        val genPermutations = PermutationUtility.generateInserts(bestPermutation.solution.toList, remainingJobs(i))
        bestPermutation = PermutationUtility.getBestPermutation(genPermutations, p)
        //println(bestPermutation)
      }
      bestPermutation
  }
  override def evaluate(problem: Problem): EvaluatedSolution = {
    val p = problem.asInstanceOf[PfsProblem]
    val pairs = p.createJobValuePairs(p.jobs, p.extractEndTimes(p.initEndTimesMatrix))
    //STEP 1: sort jobs in decreasing order, STEP 2.1.take best two,
    val sortedList = p.sortJobsDecreasing(pairs).map(x => x._1).toList
    val twoJobs = sortedList.take(2) //> twoJobs  : List[Int] = List(1, 2)
    val remainingJobs = sortedList.filterNot(twoJobs.toSet).toList //> remainingJobs  : List[Int] = List(3, 4, 5)
    
    val finalSolution = constructSolution(p, twoJobs, remainingJobs) //> mySolution  : solution.EvaluatedSolution = EvaluatedSolution(58,[I@2353b3e6| )
    finalSolution
  }
  //useless to have time limit on NEH, because it will not construct complete solution
  //by the way, NEH is pretty fast comparing to other algorithms, so it will generate
  //complete solution in polynomial time (faster than other algorihtms)
  override def evaluate(p: Problem, stopCond: StoppingCondition): EvaluatedSolution = {
    evaluate(p)
  }
  
  override def evaluate(p:Problem, seedSol: Option[Solution], stopCond: StoppingCondition):EvaluatedSolution = {
    def getSolution(seedOption: Option[Solution]) ={
      seedOption match {
        case Some(seedOption) => seedOption.evaluate(p)
        case None => evaluate(p)
      }
    }
    getSolution(seedSol)
  }
}

