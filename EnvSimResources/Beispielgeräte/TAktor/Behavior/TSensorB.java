import de.htw.saar.env.sim.device.container.Behaviour;

public class TSensorB extends Behaviour {

    public TSensorB(){
        this.setCycleTime(5000);
    }

    @Override
    public Object run(String message) {
        return null;
    }

    @Override
    public Object run() {
        return -((Math.random()*((36-30)+1))+30);
    }
}
