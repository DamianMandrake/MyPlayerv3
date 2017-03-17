package com.damian.myplayerv3.AdaptersAndListeners;


        import android.content.Context;
        import android.graphics.Bitmap;
        import android.graphics.BitmapFactory;
        import android.os.AsyncTask;
        import android.support.v7.widget.RecyclerView;
        import android.view.LayoutInflater;
        import android.view.View;
        import android.view.ViewGroup;
        import android.widget.ImageView;
        import android.widget.TextView;

        import com.damian.myplayerv3.MainActivity;
        import com.damian.myplayerv3.R;
        import com.damian.myplayerv3.Song;

        import java.util.ArrayList;

/**
 * Created by Damian on 12/30/2016.
 */
public class SongRecycler extends RecyclerView.Adapter<SongRecycler.SongViewHolder> {

    private Context ctx;
    private ArrayList<Song> songList;
    private static Song currSong;
    static private PlaySong playSong;//reference obj... is static since i have to reference this from the inner class

    public SongRecycler(Context c, ArrayList a){
        ctx=c;this.songList=new ArrayList<Song>();
        this.initSongList(a);
        System.out.println("INSIDE CTOR OF SOngRecycyler");


    }
    void initSongList(ArrayList<Song> a){
        for(Song s:a)
            this.songList.add(s);

    }



    @Override
    public SongViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(ctx).inflate(R.layout.song_recycler_item,parent,false);
        System.out.println("inside oncreateViewHolder");

        return new SongViewHolder(view,songList,ctx);
    }

    @Override
    public void onBindViewHolder(final SongViewHolder holder, int position) {
        System.out.println("Inside onBindViewHolder");

        currSong=songList.get(position);
        System.out.println("binding "+currSong.getTitle());
        if(currSong.getLargeImgPath()!=null) {
            //SongViewHolder.getFromList.setCurrSong(position);
            System.out.println("imgPath of " + currSong.getTitle() + " IS " + currSong.getLargeImgPath());


            //adding async thread here

            new AsyncTask<RecyclerView.ViewHolder,Void,Bitmap>(){

                private RecyclerView.ViewHolder v;

                @Override
                protected Bitmap doInBackground(RecyclerView.ViewHolder... viewHolders) {
                    v=viewHolders[0];
                    return BitmapFactory.decodeFile(SongRecycler.currSong.getLargeImgPath());//since inner cant access outer



                }


                @Override
                protected void onPostExecute(Bitmap bitmap) {
                    try {
                        System.out.println("BITMAP IS \n"+bitmap.toString());

                        ((SongViewHolder) v).albumArt.setImageBitmap(bitmap);
                    }catch (NullPointerException npe){
                        ((SongViewHolder) v).albumArt.setImageResource(R.drawable.notfound);
                    }
                    System.out.println("image to albumArt just got set");

                }
            }.execute(holder);


            //



        }
        else
            holder.albumArt.setImageResource(R.drawable.notfound);


        holder.songName.setText(currSong.getTitle());
        holder.artistName.setText(currSong.getArtist());

        System.out.println("Leaving onBindViewHolder");

    }



    @Override
    public int getItemCount() {
        return songList.size();
    }

    public static void setPlaySongReference(PlaySong ref){
        playSong=ref;
    }





    public static class SongViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView songName,artistName;
        ImageView albumArt;
        ArrayList<Song> tempSong;
        Context ctx;
        static GetFromList getFromList;




        public SongViewHolder(View view,ArrayList<Song> arrayList,Context c){
            super(view);
            System.out.println("****IN CTOR OF SONGVIEWHOLDER");
            view.setOnClickListener(this);
            tempSong=arrayList;
            ctx=c;
            songName=(TextView)view.findViewById(R.id.songTitle);
            artistName=(TextView)view.findViewById(R.id.artistName);
            albumArt=(ImageView) view.findViewById(R.id.songImg);


        }


        @Override
        public void onClick(View view){
            int position=getAdapterPosition();
            System.out.println("*** adapterpos is "+position);
            SongRecycler.playSong.play(position);



        }
        public static void setGetFromList(GetFromList getFromList){
            SongViewHolder.getFromList=getFromList;
        }



    }

    public interface PlaySong{
         public void play(int position);
         public void play(Song s );
        public void setSongPosition(int position);
    }



    public interface GetFromList{
        public void setCurrSong(int p);
        public String getCurrSongImgPath();
    }









}
