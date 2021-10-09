package online.anaclet.eventer;

public class Ticket {
    private int id;
    private String ticket_no;
    private int event_id;
    private int ticket_nbr;
    private  int valid;
    private String payed;
    private int used;

    public int getUsed() {
        return used;
    }

    public void setUsed(int used) {
        this.used = used;
    }

    public int getId() {
        return id;
    }

    public String getPayed() {
        return payed;
    }

    public void setPayed(String payed) {
        this.payed = payed;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTicket_no() {
        return ticket_no;
    }

    public void setTicket_no(String ticket_no) {
        this.ticket_no = ticket_no;
    }

    public int getEvent_id() {
        return event_id;
    }

    public void setEvent_id(int event_id) {
        this.event_id = event_id;
    }

    public int getTicket_nbr() {
        return ticket_nbr;
    }

    public void setTicket_nbr(int ticket_nbr) {
        this.ticket_nbr = ticket_nbr;
    }

    public int getValid() {
        return valid;
    }

    public void setValid(int valid) {
        this.valid = valid;
    }


    public Ticket(int id, String ticket_no, int event_id, int ticket_nbr, int valid, String payed, int used) {
        this.id = id;
        this.ticket_no = ticket_no;
        this.event_id = event_id;
        this.ticket_nbr = ticket_nbr;
        this.valid = valid;
        this.payed = payed;
        this.used = used;
    }
}
