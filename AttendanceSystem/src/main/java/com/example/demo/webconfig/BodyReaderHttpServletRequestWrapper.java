package com.example.demo.webconfig;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.*;
import java.util.ArrayList;
import java.util.List;


public class BodyReaderHttpServletRequestWrapper extends HttpServletRequestWrapper
{
    private final byte[] body;

    public BodyReaderHttpServletRequestWrapper(HttpServletRequest request) throws IOException
    {
        super(request);
        List<Byte> content = new ArrayList<>();
        InputStream inputStream = new BufferedInputStream(request.getInputStream());
        byte[] input = new byte[1024];
        int length = 0;
        while ((length = inputStream.read(input)) != -1)
        {
            for (int i = 0; i < length; i++)
            {
                content.add(input[i]);
            }
        }
        body = new byte[content.size()];
        for (int i = 0; i < content.size(); i++)
        {
            body[i] = content.get(i);
        }
    }

    @Override
    public BufferedReader getReader() throws IOException
    {
        return new BufferedReader(new InputStreamReader(getInputStream()));
    }

    @Override
    public ServletInputStream getInputStream() throws IOException
    {
        final ByteArrayInputStream bais = new ByteArrayInputStream(body);
        return new ServletInputStream()
        {
            @Override
            public boolean isFinished()
            {
                return false;
            }

            @Override
            public boolean isReady()
            {
                return false;
            }

            @Override
            public void setReadListener(ReadListener listener)
            {

            }

            @Override
            public int read() throws IOException
            {
                return bais.read();
            }
        };
    }

}